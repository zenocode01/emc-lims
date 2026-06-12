import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Form, Input, Select, Card, Descriptions } from 'antd'
import { PlusOutlined, DeleteOutlined, ExclamationCircleOutlined, EyeOutlined, DownloadOutlined, EditOutlined } from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import { reportTemplateApi, type ReportTemplateVO, type ReportTemplateQuery } from '../../api/report'

/**
 * 报告模板管理页面
 */
export default function ReportTemplatePage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<ReportTemplateVO | undefined>(undefined)
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailData, setDetailData] = useState<ReportTemplateVO | undefined>(undefined)
  const [form] = Form.useForm()

  const handleAdd = () => {
    form.resetFields()
    setFormData(undefined)
    setFormOpen(true)
  }

  const handleEdit = (record: ReportTemplateVO) => {
    form.setFieldsValue(record)
    setFormData(record)
    setFormOpen(true)
  }

  const handleDetail = (record: ReportTemplateVO) => {
    setDetailData(record)
    setDetailOpen(true)
  }

  const handleDelete = async (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除选中的 ${ids.length} 个模板吗？删除后不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await reportTemplateApi.deleteBatch(ids)
          message.success('删除成功')
          actionRef.current?.reload()
          setSelectedRowKeys([])
        } catch {
          // error handled by interceptor
        }
      },
    })
  }

  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    actionRef.current?.reload()
  }

  const handleExport = async () => {
    try {
      const res = await reportTemplateApi.page({
        pageNum: 1,
        pageSize: 1000,
      })
      // 转换为 CSV
      const headers = ['模板名称', '模板编码', '模板类型', '适用产品类别', '状态', '备注']
      const rows = res.records.map(item => [
        item.templateName,
        item.templateCode,
        item.templateTypeName || '-',
        item.productCategory || '-',
        item.statusName || '-',
        item.remark || '-',
      ])
      const csvContent = [headers, ...rows].map(row => row.join(',')).join('\n')
      const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `报告模板导出_${new Date().toISOString().slice(0, 10)}.csv`
      link.click()
      message.success('导出成功')
    } catch {
      message.error('导出失败')
    }
  }

  const columns: ProColumns<ReportTemplateVO>[] = [
    {
      title: '模板名称',
      dataIndex: 'templateName',
      width: 200,
    },
    {
      title: '模板编码',
      dataIndex: 'templateCode',
      width: 150,
    },
    {
      title: '模板类型',
      dataIndex: 'templateTypeName',
      width: 120,
      search: false,
    },
    {
      title: '适用产品类别',
      dataIndex: 'productCategory',
      width: 150,
      search: false,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      valueType: 'select',
      valueEnum: {
        1: { text: '启用', status: 'Success' },
        0: { text: '停用', status: 'Default' },
      },
      render: (_, record) => (
        <Tag color={record.status === 1 ? 'green' : 'default'}>
          {record.statusName || '-'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 170,
      valueType: 'dateTime',
      search: false,
      sorter: true,
    },
    {
      title: '操作',
      width: 200,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleDetail(record)}>
            <EyeOutlined /> 详情
          </a>
          <a onClick={() => handleEdit(record)}>
            <EditOutlined /> 编辑
          </a>
          <a onClick={() => handleDelete([record.id])} style={{ color: '#ff4d4f' }}>
            删除
          </a>
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<ReportTemplateVO, ReportTemplateQuery>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await reportTemplateApi.page({
            pageNum: current,
            pageSize,
            keyword: rest.keyword,
            templateType: rest.templateType,
            productCategory: rest.productCategory,
            status: rest.status,
          })
          return {
            data: res.records,
            total: res.total,
            success: true,
          }
        }}
        toolbar={{
          actions: [
            <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增模板
            </Button>,
            <Button
              key="delete"
              danger
              icon={<DeleteOutlined />}
              disabled={selectedRowKeys.length === 0}
              onClick={() => handleDelete(selectedRowKeys as number[])}
            >
              批量删除
            </Button>,
            <Button
              key="export"
              icon={<DownloadOutlined />}
              onClick={handleExport}
            >
              导出
            </Button>,
          ],
        }}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
        }}
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
          pageSizeOptions: ['10', '20', '50'],
        }}
        search={{
          labelWidth: 'auto',
          defaultCollapsed: true,
        }}
        dateFormatter="string"
      />

      {/* 新增/编辑表单 */}
      <Modal
        title={formData?.id ? '编辑报告模板' : '新增报告模板'}
        open={formOpen}
        onOk={() => form.submit()}
        onCancel={() => { setFormOpen(false); setFormData(undefined) }}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={async (values) => {
            try {
              if (formData?.id) {
                await reportTemplateApi.update({ ...values, id: formData.id })
              } else {
                await reportTemplateApi.create(values)
              }
              message.success(formData?.id ? '编辑成功' : '新增成功')
              handleFormSuccess()
            } catch {
              // error handled by interceptor
            }
          }}
        >
          <Form.Item label="模板名称" name="templateName" rules={[{ required: true, message: '请输入模板名称' }]}>
            <Input placeholder="请输入模板名称" />
          </Form.Item>
          <Form.Item label="模板编码" name="templateCode" rules={[{ required: true, message: '请输入模板编码' }]}>
            <Input placeholder="请输入模板编码" />
          </Form.Item>
          <Form.Item label="模板类型" name="templateType">
            <Select placeholder="请选择模板类型" options={[
              { label: '发射测试', value: 'emission' },
              { label: '抗扰度测试', value: 'immunity' },
              { label: '通用测试', value: 'general' },
            ]} />
          </Form.Item>
          <Form.Item label="适用产品类别" name="productCategory">
            <Select placeholder="请选择产品类别" options={[
              { label: 'ITE', value: 'ITE' },
              { label: 'Audio', value: 'Audio' },
              { label: 'Industrial', value: 'Industrial' },
              { label: 'Medical', value: 'Medical' },
              { label: 'Auto', value: 'Auto' },
            ]} />
          </Form.Item>
          <Form.Item label="状态" name="status" initialValue={1}>
            <Select options={[
              { label: '启用', value: 1 },
              { label: '停用', value: 0 },
            ]} />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 详情抽屉 */}
      <Modal
        title={`模板详情 - ${detailData?.templateName || ''}`}
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={[
          <Button key="close" onClick={() => setDetailOpen(false)}>关闭</Button>,
          <Button key="edit" type="primary" icon={<EditOutlined />} onClick={() => { setDetailOpen(false); handleDetail(detailData!); }}>编辑</Button>,
        ]}
        width={700}
      >
        {detailData && (
          <Descriptions bordered column={2} size="small">
            <Descriptions.Item label="模板名称" span={1}>{detailData.templateName}</Descriptions.Item>
            <Descriptions.Item label="模板编码" span={1}>{detailData.templateCode}</Descriptions.Item>
            <Descriptions.Item label="模板类型" span={1}>{detailData.templateTypeName || '-'}</Descriptions.Item>
            <Descriptions.Item label="适用产品类别" span={1}>{detailData.productCategory || '-'}</Descriptions.Item>
            <Descriptions.Item label="状态" span={1}>
              <Tag color={detailData.status === 1 ? 'green' : 'default'}>
                {detailData.statusName || '-'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="创建时间" span={1}>{detailData.createTime || '-'}</Descriptions.Item>
            <Descriptions.Item label="更新时间" span={1}>{detailData.updateTime || '-'}</Descriptions.Item>
            <Descriptions.Item label="备注" span={2}>{detailData.remark || '-'}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </Card>
  )
}
