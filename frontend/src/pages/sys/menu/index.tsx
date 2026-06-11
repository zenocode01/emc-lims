import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Card, Input, Tree } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, ExclamationCircleOutlined, MenuOutlined } from '@ant-design/icons'
import { sysMenuApi, type SysMenuVO } from '../../../api/sys'
import MenuForm from './MenuForm'

const { confirm } = Modal

export default function MenuPage() {
  const [menuTree, setMenuTree] = useState<SysMenuVO[]>([])
  const [formOpen, setFormOpen] = useState(false)
  const [formData, setFormData] = useState<SysMenuVO | undefined>(undefined)
  const [expandedKeys, setExpandedKeys] = useState<React.Key[]>([])

  const loadData = async () => {
    try {
      const tree = await sysMenuApi.tree()
      setMenuTree(tree)
      setExpandedKeys(tree.map((item) => item.id))
    } catch {
      // error handled
    }
  }

  const handleAdd = (parentId?: number) => {
    setFormData({ menuType: 1, status: 1, sort: 0, isHidden: 0, parentId: parentId ?? 0 })
    setFormOpen(true)
  }

  const handleEdit = (record: SysMenuVO) => {
    setFormData(record)
    setFormOpen(true)
  }

  const handleDelete = async (record: SysMenuVO) => {
    confirm({
      title: '确认删除',
      content: `确定删除菜单"${record.menuName}"吗？如果包含子菜单，将一并删除。`,
      icon: <ExclamationCircleOutlined />,
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await sysMenuApi.delete(record.id!)
          message.success('删除成功')
          loadData()
        } catch {
          // error handled
        }
      },
    })
  }

  const handleFormSuccess = () => {
    setFormOpen(false)
    setFormData(undefined)
    loadData()
  }

  const renderTree = (nodes: SysMenuVO[]): React.ReactNode => {
    return nodes.map((node) => {
      const typeTag = node.menuType === 1 ? <Tag color="blue">目录</Tag>
        : node.menuType === 2 ? <Tag color="green">菜单</Tag>
        : <Tag color="orange">按钮</Tag>

      return (
        <Tree.TreeNode
          key={node.id}
          title={
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                {node.icon && <span style={{ fontSize: 16 }}>{node.icon}</span>}
                <span>{node.menuName}</span>
                {typeTag}
              </div>
              <div style={{ display: 'flex', gap: 8 }}>
                <a onClick={() => handleEdit(node)}>
                  <EditOutlined style={{ fontSize: 14 }} />
                </a>
                <a onClick={() => handleAdd(node.id)}>
                  <PlusOutlined style={{ fontSize: 14 }} />
                </a>
                <a onClick={() => handleDelete(node)} style={{ color: '#ff4d4f' }}>
                  <DeleteOutlined style={{ fontSize: 14 }} />
                </a>
              </div>
            </div>
          }
          treeData={node.children ? renderTree(node.children) : []}
        />
      )
    })
  }

  return (
    <Card>
      <div style={{ marginBottom: 16, display: 'flex', gap: 8 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd(0)}>
          新增根菜单
        </Button>
      </div>

      <Tree
        defaultExpandAll
        treeData={renderTree(menuTree)}
        style={{ maxWidth: '100%', background: '#fafafa', padding: 16, borderRadius: 8 }}
      />
    </Card>
  )
}
