import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Descriptions, Timeline, Drawer, Form, Input, Card } from 'antd'
import {
  PlusOutlined,
  DeleteOutlined,
  HistoryOutlined,
  EyeOutlined,
  CheckOutlined,
  CloseOutlined,
  FileTextOutlined,
  ExclamationCircleOutlined,
  DownloadOutlined,
  EditOutlined,
} from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import {
  reportApi,
  type ReportVO,
  type ReportQuery,
  REPORT_STATUS_OPTIONS,
  REPORT_STATUS_COLOR,
  REPORT_ACTION_OPTIONS,
} from '../../api/report'

/**
 * 报告管理页面
 */
export default function ReportPage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [createOpen, setCreateOpen] = useState(false)
  const [editOpen, setEditOpen] = useState(false)
  const [formData, setFormData] = useState<ReportVO | undefined>(undefined)
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailData, setDetailData] = useState<ReportVO | undefined>(undefined)
  const [logDrawerOpen, setLogDrawerOpen] = useState(false)
  const [logData, setLogData] = useState<ReportVO | undefined>(undefined)
  const [logs, setLogs] = useState<any[]>([])

  /** 审核评论弹窗 */
  const [commentModalOpen, setCommentModalOpen] = useState(false)
  const [commentModalAction, setCommentModalAction] = useState<{
    type: 'approve' | 'reject' | 'submit'
    id: number
  } | null>(null)
  const [comment, setComment] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [form] = Form.useForm()

  /** 新增报告 */
  const handleCreate = () => {
    setFormData(undefined)
    setCreateOpen(true)
  }

  /** 查看详情 */
  const handleDetail = async (record: ReportVO) => {
    try {
      const res = await reportApi.detail(record.id)
      setDetailData(res)
      setDetailOpen(true)
    } catch {
      setDetailData(record)
      setDetailOpen(true)
    }
  }

  /** 编辑报告 */
  const handleEdit = async (record: ReportVO) => {
    try {
      const res = await reportApi.detail(record.id)
      setFormData(res)
      setEditOpen(true)
    } catch {
      setFormData(record)
      setEditOpen(true)
    }
  }

  /** 报告编辑提交 */
  const handleEditSubmit = async (values: any) => {
    try {
      await reportApi.update({ ...values, id: formData!.id })
      message.success('编辑成功')
      setEditOpen(false)
      setFormData(undefined)
      actionRef.current?.reload()
    } catch {
      // error handled by interceptor
    }
  }

  /** 查看审核日志 */
  const handleLogs = async (record: ReportVO) => {
    setLogData(record)
    setLogDrawerOpen(true)
    try {
      const logsRes = await reportApi.auditLogs(record.id)
      setLogs(logsRes)
    } catch {
      setLogs([])
    }
  }

  /** 删除 */
  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: '确定删除该报告吗？',
      okText: '确认删除',
      okType: 'danger',
      onOk: async () => {
        try {
          await reportApi.delete(id)
          message.success('删除成功')
          actionRef.current?.reload()
        } catch {
          // error handled
        }
      },
    })
  }

  /** 打开审核弹窗 */
  const openCommentModal = (type: 'approve' | 'reject' | 'submit', record: ReportVO) => {
    setCommentModalAction({ type, id: record.id })
    setComment('')
    setCommentModalOpen(true)
  }

  /** 提交审核操作 */
  const handleSubmitComment = async () => {
    if (!commentModalAction) return
    setSubmitting(true)
    try {
      const { id, type } = commentModalAction
      if (type === 'submit') {
        await reportApi.submit(id, comment)
        message.success('提交审核成功')
      } else if (type === 'approve') {
        await reportApi.approve(id, comment)
        message.success('审核通过')
      } else {
        await reportApi.reject(id, comment)
        message.success('已打回')
      }
      setCommentModalOpen(false)
      setComment('')
      actionRef.current?.reload()
      if (detailData?.id === commentModalAction.id) {
        const res = await reportApi.detail(commentModalAction.id)
        setDetailData(res)
      }
    } catch {
      // error handled
    } finally {
      setSubmitting(false)
    }
  }

  /** 签发报告 */
  const handleIssue = async (record: ReportVO) => {
    Modal.confirm({
      title: '确认签发',
      icon: <ExclamationCircleOutlined />,
      content: `确定签发报告 ${record.reportNo} 吗？签发后将不可修改。`,
      okText: '确认签发',
      okType: 'primary',
      onOk: async () => {
        try {
          await reportApi.issue(record.id)
          message.success('签发成功')
          actionRef.current?.reload()
          if (detailData?.id === record.id) {
            const res = await reportApi.detail(record.id)
            setDetailData(res)
          }
        } catch {
          // error handled
        }
      },
    })
  }

  /** 获取状态标签 */
  const getStatusTag = (status: string) => {
    const option = REPORT_STATUS_OPTIONS.find((o) => o.value === status)
    const color = REPORT_STATUS_COLOR[status] || 'default'
    return <Tag color={color}>{option?.label || status}</Tag>
  }

  /** 状态变更按钮渲染 */
  const renderStatusActions = (record: ReportVO) => {
    const actions: { label: string; handler: () => void }[] = []

    switch (record.status) {
      case 'draft':
        actions.push({ label: '提交审核', handler: () => openCommentModal('submit', record) })
        break
      case 'review':
        actions.push(
          { label: '审核通过', handler: () => openCommentModal('approve', record) },
          { label: '审核打回', handler: () => openCommentModal('reject', record) },
        )
        break
      case 'approved':
        actions.push({ label: '签发报告', handler: () => handleIssue(record) })
        break
      case 'rejected':
        actions.push({ label: '重新提交', handler: () => openCommentModal('submit', record) })
        break
      default:
        break
    }

    return actions.map((action) => (
      <a key={action.label} onClick={action.handler}>
        {action.label}
      </a>
    ))
  }

  const columns: ProColumns<ReportVO>[] = [
    {
      title: '报告编号',
      dataIndex: 'reportNo',
      width: 160,
      search: false,
      render: (_, record) => <a onClick={() => handleDetail(record)}>{record.reportNo}</a>,
    },
    {
      title: '样品编号',
      dataIndex: 'sampleNo',
      width: 150,
      search: false,
    },
    {
      title: '产品名称',
      dataIndex: 'productName',
      width: 150,
      ellipsis: true,
      search: false,
    },
    {
      title: '客户',
      dataIndex: 'customerName',
      width: 120,
      search: false,
    },
    {
      title: '版本',
      dataIndex: 'version',
      width: 60,
      search: false,
      render: (_, record) => `v${record.version}`,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: Object.fromEntries(
        REPORT_STATUS_OPTIONS.map((o) => [o.value, { text: o.label, status: REPORT_STATUS_COLOR[o.value] === 'processing' ? 'Processing' : (REPORT_STATUS_COLOR[o.value] === 'success' ? 'Success' : (REPORT_STATUS_COLOR[o.value] === 'warning' ? 'Warning' : 'Default')) }]),
      ),
      search: false,
      render: (_, record) => getStatusTag(record.status),
    },
    {
      title: '签发日期',
      dataIndex: 'issuedDate',
      width: 120,
      valueType: 'date',
      search: false,
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
      width: 260,
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
          <a onClick={() => handleLogs(record)}>
            <HistoryOutlined /> 日志
          </a>
          {renderStatusActions(record)}
          {record.status === 'draft' && (
            <a onClick={() => handleDelete(record.id)} style={{ color: '#ff4d4f' }}>
              删除
            </a>
          )}
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<ReportVO, ReportQuery>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await reportApi.page({
            pageNum: current,
            pageSize,
            sampleId: rest.sampleId,
            customerId: rest.customerId,
            status: rest.status,
            startDate: rest.startDate,
            endDate: rest.endDate,
          })
          return {
            data: res.records,
            total: res.total,
            success: true,
          }
        }}
        toolbar={{
          actions: [
            <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新建报告
            </Button>,
            <Button
              key="export"
              icon={<DownloadOutlined />}
              onClick={() => window.open('/api/report/export', '_blank')}
            >
              导出Excel
            </Button>,
          ],
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

      {/* 新建报告弹窗 */}
      <Modal
        title="新建报告"
        open={createOpen}
        onOk={() => form.submit()}
        onCancel={() => { setCreateOpen(false); setFormData(undefined) }}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={async (values) => {
          await reportApi.create(values as any)
          message.success('创建成功')
          setCreateOpen(false)
          form.resetFields()
          actionRef.current?.reload()
        }}>
          <Form.Item label="样品编号" name="sampleId" rules={[{ required: true, message: '请输入样品编号' }]}>
            <Input placeholder="请输入样品编号" />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={2} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 详情抽屉 */}
      <Drawer
        title={`报告详情 - ${detailData?.reportNo || ''}`}
        placement="right"
        width={700}
        open={detailOpen}
        onClose={() => setDetailOpen(false)}
      >
        {detailData && (
          <Descriptions bordered column={2} size="small">
            <Descriptions.Item label="报告编号" span={1}>{detailData.reportNo}</Descriptions.Item>
            <Descriptions.Item label="状态" span={1}>{getStatusTag(detailData.status)}</Descriptions.Item>
            <Descriptions.Item label="版本" span={1}>v{detailData.version}</Descriptions.Item>
            <Descriptions.Item label="样品编号" span={1}>{detailData.sampleNo || '-'}</Descriptions.Item>
            <Descriptions.Item label="客户" span={1}>{detailData.customerName || '-'}</Descriptions.Item>
            <Descriptions.Item label="产品名称" span={1}>{detailData.productName || '-'}</Descriptions.Item>
            <Descriptions.Item label="审核人" span={1}>{detailData.reviewerName || '-'}</Descriptions.Item>
            <Descriptions.Item label="批准人" span={1}>{detailData.approverName || '-'}</Descriptions.Item>
            <Descriptions.Item label="签发日期" span={1}>{detailData.issuedDate || '-'}</Descriptions.Item>
            <Descriptions.Item label="文件链接" span={1}>{detailData.fileUrl ? <a href={detailData.fileUrl} target="_blank" rel="noopener noreferrer">查看文件</a> : '-'}  </Descriptions.Item>
            <Descriptions.Item label="备注" span={2}>{detailData.remark || '-'}</Descriptions.Item>
            <Descriptions.Item label="创建时间" span={1}>{detailData.createTime}</Descriptions.Item>
            <Descriptions.Item label="更新时间" span={1}>{detailData.updateTime}</Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>

      {/* 审核日志抽屉 */}
      <Drawer
        title={`审核日志 - ${logData?.reportNo || ''}`}
        placement="right"
        width={600}
        open={logDrawerOpen}
        onClose={() => setLogDrawerOpen(false)}
      >
        {logs.length > 0 ? (
          <Timeline
            items={logs.map((log) => ({
              color: REPORT_ACTION_OPTIONS.find((o) => o.value === log.action)?.label === '打回' ? 'red' : 'blue',
              children: (
                <div>
                  <div style={{ fontWeight: 500 }}>
                    {log.actionName || log.action}
                  </div>
                  {log.comment && <div style={{ color: '#666', fontSize: 12, marginTop: 4 }}>{log.comment}</div>}
                  <div style={{ color: '#999', fontSize: 12, marginTop: 4 }}>
                    {log.operatorName || '系统'} · {log.auditTime}
                  </div>
                </div>
              ),
            }))}
          />
        ) : (
          <div style={{ textAlign: 'center', color: '#999', padding: '40px 0' }}>暂无审核日志</div>
        )}
      </Drawer>

      {/* 审核评论弹窗 */}
      <Modal
        title={
          commentModalAction?.type === 'approve' ? '审核通过' :
          commentModalAction?.type === 'reject' ? '审核打回' : '提交审核'
        }
        open={commentModalOpen}
        onOk={handleSubmitComment}
        onCancel={() => { setCommentModalOpen(false); setComment('') }}
        confirmLoading={submitting}
        okText="确认"
        cancelText="取消"
      >
        <Form layout="vertical">
          <Form.Item label="审核意见">
            <Input.TextArea
              rows={3}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="请输入审核意见（可选）"
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 报告编辑抽屉 */}
      <Drawer
        title={`编辑报告 - ${formData?.reportNo || ''}`}
        placement="right"
        width={600}
        open={editOpen}
        onClose={() => { setEditOpen(false); setFormData(undefined) }}
        extra={
          <Button type="primary" onClick={handleEditSubmit} disabled={!formData}>
            保存
          </Button>
        }
      >
        {formData && (
          <Form layout="vertical" onFinish={handleEditSubmit} initialValues={formData}>
            <Form.Item label="报告编号" name="reportNo" rules={[{ required: true }]}>
              <Input readOnly />
            </Form.Item>
            <Form.Item label="样品编号" name="sampleNo">
              <Input />
            </Form.Item>
            <Form.Item label="产品名称" name="productName">
              <Input />
            </Form.Item>
            <Form.Item label="客户" name="customerName">
              <Input />
            </Form.Item>
            <Form.Item label="备注" name="remark">
              <Input.TextArea rows={3} />
            </Form.Item>
          </Form>
        )}
      </Drawer>
    </Card>
  )
}
