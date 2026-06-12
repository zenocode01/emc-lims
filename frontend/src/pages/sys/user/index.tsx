import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Switch, Card, Select, Input } from 'antd'
import { PlusOutlined, DeleteOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import { sysUserApi, type SysUserVO, type SysUserQuery } from '../../../api/sys'
import UserForm from './UserForm'
import { DownloadOutlined } from '@ant-design/icons'

export default function UserPage() {
  const actionRef = useRef<ActionType>(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<SysUserVO | undefined>(undefined)
  const [deptOptions, setDeptOptions] = useState<{ label: string; value: number }[]>([])
  const [roleOptions, setRoleOptions] = useState<{ label: string; value: number }[]>([])

  const handleAdd = () => {
    setFormData(undefined)
    setFormOpen(true)
  }

  const handleEdit = async (record: SysUserVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  const handleDelete = async (ids: number[]) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: `确定删除选中的 ${ids.length} 个用户吗？删除后不可恢复。`,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await sysUserApi.deleteBatch(ids)
          message.success('删除成功')
          actionRef.current?.reload()
          setSelectedRowKeys([])
        } catch {
          // error handled by interceptor
        }
      },
    })
  }

  const handleStatusChange = async (id: number, status: number) => {
    try {
      await sysUserApi.updateStatus(id, status)
      message.success(status === 1 ? '已启用' : '已禁用')
      actionRef.current?.reload()
    } catch {
      // error handled by interceptor
    }
  }

  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    actionRef.current?.reload()
  }

  const columns: ProColumns<SysUserVO>[] = [
    {
      title: '用户名',
      dataIndex: 'username',
      width: 120,
      search: false,
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      width: 120,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      width: 130,
    },
    {
      title: '部门',
      dataIndex: 'deptName',
      width: 150,
      search: false,
    },
    {
      title: '角色',
      dataIndex: 'roleName',
      width: 120,
      search: false,
      render: (_, record) =>
        record.roleName ? <Tag color="blue">{record.roleName}</Tag> : '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      valueType: 'select',
      valueEnum: {
        1: { text: '启用', status: 'Success' },
        0: { text: '禁用', status: 'Error' },
      },
      render: (_, record) => (
        <Switch
          checked={record.status === 1}
          size="small"
          onChange={(checked) => handleStatusChange(record.id, checked ? 1 : 0)}
        />
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 170,
      valueType: 'dateTime',
      search: false,
    },
    {
      title: '操作',
      width: 120,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleEdit(record)}>编辑</a>
          <a onClick={() => handleDelete([record.id])} style={{ color: '#ff4d4f' }}>
            删除
          </a>
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<SysUserVO, SysUserQuery>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await sysUserApi.page({
            pageNum: current,
            pageSize,
            keyword: rest.keyword,
            deptId: rest.deptId,
            roleId: rest.roleId,
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
              新增用户
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
              onClick={() => window.open('/api/sys/user/export', '_blank')}
            >
              导出Excel
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

      <UserForm
        open={formOpen}
        data={formData}
        deptOptions={deptOptions}
        roleOptions={roleOptions}
        onSuccess={handleFormSuccess}
        onCancel={() => {
          setFormOpen(false)
          setFormData(undefined)
        }}
      />
    </Card>
  )
}
